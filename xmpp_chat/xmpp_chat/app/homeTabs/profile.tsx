/** 
 * this module is responsible for the following functions
 * - display current user information or another one
 * - define custom status message
 * 
*/
import { Alert, Button, Text, TextInput, View } from "react-native";
import { ThemedTextInput } from "@/components/ThemedTextInput";
import React from "react"
import { HomeScreenRouteProp } from "@/constants/Props";
import { useRoute } from "@react-navigation/native";
const Profile : React.FC = () => {
  const route = useRoute<HomeScreenRouteProp>();
  const {username} = route.params
  return (
    <View
    style={{
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      gap: 20,
    }}
  >
   <Text>{username} Settings</Text>
   <Text>Profile Screen</Text>
  </View>
  )
}
export default Profile