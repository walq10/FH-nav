package com.fhnav.app.data.remote.amap

import android.content.Context
import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.model.AMapCalcRouteResult
import com.amap.api.navi.model.AMapLaneInfo
import com.amap.api.navi.model.AMapModelCross
import com.amap.api.navi.model.AMapNaviCameraInfo
import com.amap.api.navi.model.AMapNaviCross
import com.amap.api.navi.model.AMapNaviInfo
import com.amap.api.navi.model.AMapNaviLocation
import com.amap.api.navi.model.AMapNaviRouteNotifyData
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo
import com.amap.api.navi.model.AMapServiceAreaInfo
import com.amap.api.navi.model.AMapTrafficStatus
import com.amap.api.navi.model.NaviInfo
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.amap.api.services.route.BusRouteResult
import com.amap.api.services.route.DrivePath
import com.amap.api.services.route.DriveRouteResult
import com.amap.api.services.route.RideRouteResult
import com.amap.api.services.route.RouteSearch
import com.amap.api.services.route.WalkRouteResult
import com.fhnav.app.data.model.NavigationEvent
import com.fhnav.app.data.model.Route
import com.fhnav.app.data.repository.AMapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class AMapRepositoryImpl(
    private val context: Context
) : AMapRepository {

    private val navigationEvents = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 32)

    private var aMapNavi: AMapNavi? = null

    override suspend fun searchPoi(keyword: String, city: String): Result<List<PoiItem>> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                val query = PoiSearch.Query(keyword, "", city)
                query.pageSize = 20
                query.pageNum = 0

                val poiSearch = PoiSearch(context, query)
                poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
                    override fun onPoiSearched(result: PoiResult?, resultCode: Int) {
                        if (cont.isActive) {
                            if (resultCode == 1000 && result != null) {
                                cont.resume(Result.success(result.pois ?: emptyList()))
                            } else {
                                cont.resume(Result.failure(Exception("POI search failed: code=$resultCode")))
                            }
                        }
                    }

                    override fun onPoiItemSearched(item: PoiItem?, resultCode: Int) {
                        // Not used for keyword search
                    }
                })
                poiSearch.searchPOIAsyn()

                cont.invokeOnCancellation {
                    // Cleanup if cancelled
                }
            }
        }
    }

    override suspend fun planRoute(from: LatLng, to: LatLng): Result<List<Route>> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                val fromPoint = LatLonPoint(from.latitude, from.longitude)
                val toPoint = LatLonPoint(to.latitude, to.longitude)

                val fromAndTo = RouteSearch.FromAndTo(fromPoint, toPoint)
                val query = RouteSearch.DriveRouteQuery(
                    fromAndTo,
                    RouteSearch.DrivingDefault,
                    null,
                    null,
                    ""
                )
                query.isUseDefaultFee = true

                val routeSearch = RouteSearch(context)
                routeSearch.setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
                    override fun onDriveRouteSearched(result: DriveRouteResult?, resultCode: Int) {
                        if (cont.isActive) {
                            if (resultCode == 1000 && result != null) {
                                val routes = result.paths?.mapIndexed { index, drivePath ->
                                    mapDrivePathToRoute(drivePath, index, result.paths.indexOf(drivePath) == 0)
                                } ?: emptyList()
                                cont.resume(Result.success(routes))
                            } else {
                                cont.resume(Result.failure(Exception("Route planning failed: code=$resultCode")))
                            }
                        }
                    }

                    override fun onBusRouteSearched(result: BusRouteResult?, resultCode: Int) {}
                    override fun onWalkRouteSearched(result: WalkRouteResult?, resultCode: Int) {}
                    override fun onRideRouteSearched(result: RideRouteResult?, resultCode: Int) {}
                })
                routeSearch.calculateDriveRouteAsyn(query)

                cont.invokeOnCancellation { }
            }
        }
    }

    private fun mapDrivePathToRoute(path: DrivePath, index: Int, isRecommended: Boolean): Route {
        val polylinePoints = mutableListOf<LatLng>()
        path.steps?.forEach { step ->
            step.polyline?.forEach { point ->
                polylinePoints.add(LatLng(point.latitude, point.longitude))
            }
        }

        val colors = intArrayOf(Color.BLUE, Color.GREEN, Color.parseColor("#FF8C00"))
        val routeColor = if (index < colors.size) colors[index] else Color.GRAY

        return Route(
            id = "route_$index",
            distance = path.distance, // meters
            duration = path.duration, // seconds
            tollCost = path.tolls.toDouble(),
            trafficLights = path.trafficLights,
            polylinePoints = polylinePoints,
            isRecommended = isRecommended,
            routeColor = routeColor
        )
    }

    override fun getNavigationEvents(): Flow<NavigationEvent> = navigationEvents

    override suspend fun startNavigation(route: Route) {
        withContext(Dispatchers.Main) {
            val navi = aMapNavi ?: AMapNavi.getInstance(context).also { aMapNavi = it }

            navi.addAMapNaviListener(createNaviListener())

            // Convert route points to NaviLatLng
            val naviPoints = route.polylinePoints.map { NaviLatLng(it.latitude, it.longitude) }

            // Start GPS navigation
            navi.startNavi(NaviType.GPS)
            navigationEvents.emit(NavigationEvent.Started)
        }
    }

    override suspend fun stopNavigation() {
        withContext(Dispatchers.Main) {
            aMapNavi?.stopNavi()
            aMapNavi?.destroy()
            aMapNavi = null
        }
    }

    private fun createNaviListener(): AMapNaviListener {
        return object : AMapNaviListener {
            override fun onNaviInfoUpdate(naviInfo: NaviInfo?) {
                if (naviInfo == null) return

                val event = when {
                    naviInfo.m_NaviType == 1 -> NavigationEvent.Arrived
                    naviInfo.iconType == 1 -> NavigationEvent.GoStraight
                    naviInfo.iconType == 2 -> NavigationEvent.TurnLeft(naviInfo.currentRemainDistance)
                    naviInfo.iconType == 3 -> NavigationEvent.TurnRight(naviInfo.currentRemainDistance)
                    naviInfo.iconType == 4 -> NavigationEvent.UTurn
                    naviInfo.iconType == 5 -> NavigationEvent.KeepLeft(naviInfo.currentRemainDistance)
                    naviInfo.iconType == 6 -> NavigationEvent.KeepRight(naviInfo.currentRemainDistance)
                    naviInfo.iconType == 7 -> NavigationEvent.EnterRoundabout
                    else -> NavigationEvent.GoStraight
                }

                try { navigationEvents.tryEmit(event) } catch (_: Exception) {}
            }

            override fun onNaviInfoUpdate(p0: AMapNaviInfo?) {}

            override fun onCalculateRouteSuccess(result: AMapCalcRouteResult?) {
                // Route calculated, start guidance
            }

            override fun onCalculateRouteFailure(p0: AMapCalcRouteResult?) {
                try { navigationEvents.tryEmit(NavigationEvent.OffRoute) } catch (_: Exception) {}
            }

            override fun onServiceAreaUpdate(p0: Array<out AMapServiceAreaInfo>?) {}

            override fun onReCalculateRouteForYaw() {
                try { navigationEvents.tryEmit(NavigationEvent.OffRoute) } catch (_: Exception) {}
            }

            override fun onReCalculateRouteForTrafficJam() {}

            override fun onArriveDestination() {
                try { navigationEvents.tryEmit(NavigationEvent.Arrived) } catch (_: Exception) {}
            }

            override fun onArriveWayPoint(p0: Int) {}

            override fun onCalculateRouteSuccess(p0: IntArray?) {}

            override fun onCalculateRouteFailure(p0: Int) {}

            override fun onStartNavi(p0: Int) {}

            override fun onTrafficStatusUpdate() {}

            override fun onLocationChange(p0: AMapNaviLocation?) {}

            override fun onGetNavigationText(p0: Int, p1: String?) {}

            override fun onGetNavigationText(p0: String?) {}

            override fun onEndEmulatorNavi() {}

            override fun onArrivedWayPoint(p0: Int) {}

            override fun onInitNaviFailure() {
                try { navigationEvents.tryEmit(NavigationEvent.OffRoute) } catch (_: Exception) {}
            }

            override fun onInitNaviSuccess() {}

            override fun onReCalculateRouteForYaw(p0: AMapNaviRouteNotifyData?) {
                try { navigationEvents.tryEmit(NavigationEvent.OffRoute) } catch (_: Exception) {}
            }

            override fun onReCalculateRouteForTrafficJam(p0: AMapNaviRouteNotifyData?) {}

            override fun onArrivedDestination(p0: AMapNaviRouteNotifyData?) {
                try { navigationEvents.tryEmit(NavigationEvent.Arrived) } catch (_: Exception) {}
            }

            override fun onPlayRing(p0: Int) {}

            override fun onNaviRouteNotify(p0: AMapNaviRouteNotifyData?) {}

            override fun onGpsOpenStatus(p0: Boolean) {}

            override fun updateCameraInfo(p0: Array<out AMapNaviCameraInfo>?) {
                try { navigationEvents.tryEmit(NavigationEvent.SpeedCamera) } catch (_: Exception) {}
            }

            override fun onServiceAreaUpdate(p0: AMapNaviRouteNotifyData?) {}

            override fun updateAimlessModeStatistics(p0: Int) {}

            override fun updateAimlessModeCongestionInfo(p0: AMapTrafficStatus?) {}

            override fun onNaviTurnClick() {}

            override fun onNaviMapMode(p0: Int) {}

            override fun onNaviCancel() {}

            override fun onNaviSetting() {}

            override fun onNaviMapModeChanged(p0: Int) {}

            override fun onNaviDirectionChanged(p0: Int) {}

            override fun onNaviStraightAhead() {}

            override fun onNaviBusy(p0: Boolean) {}

            override fun onNaviTypeUpdate(p0: Int) {}

            override fun onNaviBackClick(): Boolean = false

            override fun isCalculateDiskRoute(): Boolean = false

            override fun onNaviRouteUpdate(p0: AMapNaviRouteNotifyData?) {}

            override fun showModeCross(p0: AMapNaviCross?) {}

            override fun hideModeCross() {}

            override fun showLaneInfo(p0: Array<out AMapLaneInfo>?) {}

            override fun hideLaneInfo() {}

            override fun showCross(p0: AMapNaviCross?) {}

            override fun hideCross() {}

            override fun showModeCross(p0: AMapModelCross?) {}

            override fun onLockScreen(p0: Boolean) {}

            override fun onNaviViewLoaded() {}
        }
    }
}
