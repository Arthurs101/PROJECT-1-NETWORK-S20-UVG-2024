import { RouteProp } from "@react-navigation/native";
import { NativeStackNavigationProp, NativeStackScreenProps } from "@react-navigation/native-stack";
export type RootStackParamList = {
    index: undefined;
    homeTabs: {username:string};
  };

export type HomeScreenRouteProp = RouteProp<{ params: { username: string } }, 'params'>;
export type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'index'>;

// on the home tabs layouts
export type HomeTabsParamList = {
    home: { username: string };
    profile: { username: string };
  };
  
