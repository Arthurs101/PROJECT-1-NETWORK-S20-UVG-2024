import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/Ionicons';
import Home from './home';
import Profile from './profile';

type HomeTabsParamList = {
  home: undefined;
  profile: undefined;
};

const Tab = createBottomTabNavigator<HomeTabsParamList>();

export default function HomeTabsLayout() {
  return (
    <Tab.Navigator>
      <Tab.Screen 
        name="home" 
        component={Home} 
        options={{
          tabBarIcon: ({ color, size }) => (
            <Icon name="home-outline" color={color} size={size} />
          ),
        }}
      />
      <Tab.Screen 
        name="profile" 
        component={Profile} 
        options={{
          tabBarIcon: ({ color, size }) => (
            <Icon name="person-outline" color={color} size={size} />
          ),
        }}
      />
    </Tab.Navigator>
  );
}
