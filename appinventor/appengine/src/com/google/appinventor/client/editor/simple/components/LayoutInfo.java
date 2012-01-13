// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.appinventor.client.editor.simple.components;

import java.util.List;
import java.util.Map;

/**
 * The layout information for a component.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
abstract class LayoutInfo {
  Map<MockComponent, LayoutInfo> layoutInfoMap;
  MockComponent component;
  List<MockComponent> visibleChildren;
  int width;   // Does not include the MockComponent's CSS border
  int height;  // Does not include the MockComponent's CSS border

  protected LayoutInfo(Map<MockComponent, LayoutInfo> layoutInfoMap, MockComponent component) {
    this.layoutInfoMap = layoutInfoMap;
    this.component = component;
    visibleChildren = component.getShowingVisibleChildren();
    width = component.getWidthHint();
    height = component.getHeightHint();
    layoutInfoMap.put(component, this);
  }

  final void gatherDimensions() {
    prepareToGatherDimensions();

    if (width == MockVisibleComponent.LENGTH_PREFERRED) {
      calculateAndStoreAutomaticWidth();
    }
    if (height == MockVisibleComponent.LENGTH_PREFERRED) {
      calculateAndStoreAutomaticHeight();
    }
  }

  protected void prepareToGatherDimensions() {
  }

  void calculateAndStoreAutomaticWidth() {
    width = calculateAutomaticWidth();
  }

  void calculateAndStoreAutomaticHeight() {
    height = calculateAutomaticHeight();
  }

  abstract int calculateAutomaticWidth();

  abstract int calculateAutomaticHeight();

  void cleanUp() {
    layoutInfoMap = null;
    component = null;
    visibleChildren = null;
  }
}
