/** 
 * this module is responsible for the following functions
 * - display current user information or another one
 * - define custom status message
 * 
*/
import { Alert, Button, Text, TextInput, View } from "react-native";
import { ThemedTextInput } from "@/components/ThemedTextInput";
export default function Profile() {
  return (
    <View
    style={{
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      gap: 20,
    }}
  >
   <Text>Profile Screen</Text>
  </View>
  )
}