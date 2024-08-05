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
import React from "react";
import { useRoute } from "@react-navigation/native";
import { HomeScreenRouteProp } from "@/constants/Props";
const Home: React.FC = () => {
  const route = useRoute<HomeScreenRouteProp>();
  const {username} = route.params;
  return (
    <View
    style={{
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      gap: 20,
    }}
  >
   <Text>Welcome {username}!</Text>
  </View>
  )
}

export default Home