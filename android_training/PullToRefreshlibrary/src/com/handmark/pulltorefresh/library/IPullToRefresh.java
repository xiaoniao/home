/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.view.View;
import android.view.animation.Interpolator;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;

/**
 * 刷新接口
 */
public interface IPullToRefresh<T extends View> { // T 是真正的容器 例如 ListView WebView

	/********************************************************** get is 获得属性****************************************************************/
	
	/**
	 * Demos the Pull-to-Refresh functionality to the user so that they are
	 * aware it is there. This could be useful when the user first opens your
	 * app, etc. The animation will only happen if the Refresh View (ListView,
	 * ScrollView, etc) is in a state where a Pull-to-Refresh could occur by a
	 * user's touch gesture (i.e. scrolled to the top/bottom).
	 * 
	 * @return true - if the Demo has been started, false if not.
	 */
	public boolean demo(); // 演示下拉刷新的功能给用户，true演示已经开始,false反之

	/**
	 * Get the mode that this view is currently in. This is only really useful
	 * when using <code>Mode.BOTH</code>.
	 * 
	 * @return Mode that the view is currently in
	 */
	public Mode getCurrentMode(); // 返回当前的Mode模式 这个一般只在Mode.BOTH时使用

	/**
	 * Returns whether the Touch Events are filtered or not. If true is
	 * returned, then the View will only use touch events where the difference
	 * in the Y-axis is greater than the difference in the X-axis. This means
	 * that the View will not interfere when it is used in a horizontal
	 * scrolling View (such as a ViewPager).
	 * 
	 * @return boolean - true if the View is filtering Touch Events
	 */
	public boolean getFilterTouchEvents(); // 返回触摸事件是否被过滤

	/**
	 * Returns a proxy object which allows you to call methods on all of the
	 * LoadingLayouts (the Views which show when Pulling/Refreshing).
	 * <p />
	 * You should not keep the result of this method any longer than you need
	 * it.
	 * 
	 * @return Object which will proxy any calls you make on it, to all of the
	 *         LoadingLayouts.
	 */
	public ILoadingLayout getLoadingLayoutProxy(); // 返回LoadingLayouts接口,用ILoadingLayout操作LoadingLayout

	/**
	 * Returns a proxy object which allows you to call methods on the
	 * LoadingLayouts (the Views which show when Pulling/Refreshing). The actual
	 * LoadingLayout(s) which will be affected, are chosen by the parameters you
	 * give.
	 * <p />
	 * You should not keep the result of this method any longer than you need
	 * it.
	 * 
	 * @param includeStart - Whether to include the Start/Header Views
	 * @param includeEnd - Whether to include the End/Footer Views
	 * @return Object which will proxy any calls you make on it, to the
	 *         LoadingLayouts included.
	 */
	public ILoadingLayout getLoadingLayoutProxy(boolean includeStart, boolean includeEnd); // 同上，多了两参数，是否包含headview是否包含footview

	/**
	 * Get the mode that this view has been set to. If this returns
	 * <code>Mode.BOTH</code>, you can use <code>getCurrentMode()</code> to
	 * check which mode the view is currently in
	 * 
	 * @return Mode that the view has been set to
	 */
	public Mode getMode(); // 返回这个View设置过的Mode,如果返回Mode.BOTH你应该和getCurrentMode比较mode是否一样

	/**
	 * Get the Wrapped Refreshable View. Anything returned here has already been
	 * added to the content view.
	 * 
	 * @return The View which is currently wrapped
	 */
	public T getRefreshableView(); // 返回真正的view,例如ListView WebView

	/**
	 * Get whether the 'Refreshing' View should be automatically shown when
	 * refreshing. Returns true by default.
	 * 
	 * @return - true if the Refreshing View will be show
	 */
	public boolean getShowViewWhileRefreshing(); // 返回'Refreshing' View是否自动显示

	/**
	 * @return - The state that the View is currently in.
	 */
	public State getState(); // 返回当前State状态

	/**
	 * Whether Pull-to-Refresh is enabled
	 * 
	 * @return enabled
	 */
	public boolean isPullToRefreshEnabled(); // 返回下拉刷新是否可用

	/**
	 * Gets whether Overscroll support is enabled. This is different to
	 * Android's standard Overscroll support (the edge-glow) which is available
	 * from GINGERBREAD onwards
	 * 
	 * @return true - if both PullToRefresh-OverScroll and Android's inbuilt
	 *         OverScroll are enabled
	 */
	public boolean isPullToRefreshOverScrollEnabled(); // 是否支持Overscroll，这个Overscroll和系统的Overscroll不一样,系统的是从2.3开始

	/**
	 * Returns whether the Widget is currently in the Refreshing mState
	 * 
	 * @return true if the Widget is currently refreshing
	 */
	public boolean isRefreshing(); // 返回是否正在刷新

	/**
	 * Returns whether the widget has enabled scrolling on the Refreshable View
	 * while refreshing.
	 * 
	 * @return true if the widget has enabled scrolling while refreshing
	 */
	public boolean isScrollingWhileRefreshingEnabled(); // 返回当刷新的时候scrolling是否可见

	/**
	 * Mark the current Refresh as complete. Will Reset the UI and hide the
	 * Refreshing View
	 */
	public void onRefreshComplete(); // 下拉刷新执行完毕，它会重置UI和隐藏Refreshing View
	
	
	/********************************************************** set 设置属性****************************************************************/
	
	/**
	 * Set the Touch Events to be filtered or not. If set to true, then the View
	 * will only use touch events where the difference in the Y-axis is greater
	 * than the difference in the X-axis. This means that the View will not
	 * interfere when it is used in a horizontal scrolling View (such as a
	 * ViewPager), but will restrict which types of finger scrolls will trigger
	 * the View.
	 * 
	 * @param filterEvents - true if you want to filter Touch Events. Default is
	 *            true.
	 */
	public void setFilterTouchEvents(boolean filterEvents); // 是否设置过滤事件

	/**
	 * Set the mode of Pull-to-Refresh that this view will use.
	 * 
	 * @param mode - Mode to set the View to
	 */
	public void setMode(Mode mode); // 设置Mode模式

	/**
	 * Set OnPullEventListener for the Widget
	 * 
	 * @param listener - Listener to be used when the Widget has a pull event to
	 *            propogate.
	 */
	public void setOnPullEventListener(OnPullEventListener<T> listener); // 设置OnPullEventListener监听  -->这个监听用来传递?

	/**
	 * Set OnRefreshListener for the Widget
	 * 
	 * @param listener - Listener to be used when the Widget is set to Refresh
	 */
	public void setOnRefreshListener(OnRefreshListener<T> listener); // 设置刷新事件OnRefreshListener

	/**
	 * Set OnRefreshListener for the Widget
	 * 
	 * @param listener - Listener to be used when the Widget is set to Refresh
	 */
	public void setOnRefreshListener(OnRefreshListener2<T> listener); // 设置刷新事件OnRefreshListener2

	/**
	 * Sets whether Overscroll support is enabled. This is different to
	 * Android's standard Overscroll support (the edge-glow). This setting only
	 * takes effect when running on device with Android v2.3 or greater.
	 * 
	 * @param enabled - true if you want Overscroll enabled
	 */
	public void setPullToRefreshOverScrollEnabled(boolean enabled); // 设置是否显示Overscroll，这个设置只在2.3以上管用

	/**
	 * Sets the Widget to be in the refresh state. The UI will be updated to
	 * show the 'Refreshing' view, and be scrolled to show such.
	 */
	public void setRefreshing(); // 设置正在刷新状态，UI会显示'Refreshing' View,并滚动到这个位置显示

	/**
	 * Sets the Widget to be in the refresh state. The UI will be updated to
	 * show the 'Refreshing' view.
	 * 
	 * @param doScroll - true if you want to force a scroll to the Refreshing
	 *            view.
	 */
	public void setRefreshing(boolean doScroll); // 设置正在刷新状态，UI会显示'Refreshing' View,如果doScroll为true则会自动滚动到该位置，否则不会。

	/**
	 * Sets the Animation Interpolator that is used for animated scrolling.
	 * Defaults to a DecelerateInterpolator
	 * 
	 * @param interpolator - Interpolator to use
	 */
	public void setScrollAnimationInterpolator(Interpolator interpolator); // 设置动画速率

	/**
	 * By default the Widget disables scrolling on the Refreshable View while
	 * refreshing. This method can change this behaviour.
	 * 
	 * @param scrollingWhileRefreshingEnabled - true if you want to enable
	 *            scrolling while refreshing
	 */
	public void setScrollingWhileRefreshingEnabled(boolean scrollingWhileRefreshingEnabled); // 设置刷新是是否显示scrolling

	/**
	 * A mutator to enable/disable whether the 'Refreshing' View should be
	 * automatically shown when refreshing.
	 * 
	 * @param showView
	 */
	public void setShowViewWhileRefreshing(boolean showView); // 设置'Refreshing' View在刷新的时候是否会自动显示

}