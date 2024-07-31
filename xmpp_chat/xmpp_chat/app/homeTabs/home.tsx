/*
This module will be responsible for the following operations:
sending messages
    -direct message service
    -group message service
    -add friends ( search for the user and send a request??)
    -show notifications of new mesages (probably)
*/
import { Alert, Button, Text, TextInput, View } from "react-native";
import { ThemedTextInput } from "@/components/ThemedTextInput";
export default function Home() {
  return (
    <View
    style={{
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      gap: 20,
    }}
  >
   <Text>Home Screen</Text>
  </View>
  )
}