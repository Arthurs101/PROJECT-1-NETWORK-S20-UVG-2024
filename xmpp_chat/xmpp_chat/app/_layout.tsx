import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Index from './index'; // Ensure this path is correct
import HomeTabs from './homeTabs/_layout'; // Ensure this path is correct

type RootStackParamList = {
  index: undefined;
  homeTabs: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootLayout() {
  return (
      <Stack.Navigator initialRouteName="index">
        <Stack.Screen name="index" component={Index} options={{ headerShown: false }} />
        <Stack.Screen name="homeTabs" component={HomeTabs} options={{ headerShown: false }} />
      </Stack.Navigator>
  );
}
