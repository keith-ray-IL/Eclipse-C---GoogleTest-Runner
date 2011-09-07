#!/bin/bash

cd ~/workspace/gtest_eclipse_plugin/archive/
rm GTest_EclipsePlugin.zip
mkdir tmp  tmp/plugins tmp/features
cd tmp
cp /Applications/eclipse/plugins/gtest.eclipse.plugin* ./plugins/
cp -r /Applications/eclipse/features/gtest.eclipse.plugin* ./features/
zip -r -q -9 GTest_EclipsePlugin.zip .
mv GTest_EclipsePlugin.zip ..
rm -r ../tmp