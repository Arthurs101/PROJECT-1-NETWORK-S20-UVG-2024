import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/Ionicons';
import Home from './home';
import Profile from './profile';
import { HomeTabsParamList } from '@/constants/Props';
import { RouteProp, useRoute } from '@react-navigation/native';

const Tab = createBottomTabNavigator<HomeTabsParamList>();

export default function HomeTabsLayout() {
  const route = useRoute<RouteProp<{ homeTabs: { username: string } }, 'homeTabs'>>();
  const { username } = route.params;
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
        initialParams={{username}} 
      />
      <Tab.Screen 
        name="profile" 
        component={Profile} 
        options={{
          tabBarIcon: ({ color, size }) => (
            <Icon name="person-outline" color={color} size={size} />
          ),
        }}
        initialParams={{username}} 
      />
    </Tab.Navigator>
  );
}
