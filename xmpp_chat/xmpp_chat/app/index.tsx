import React, { useState } from 'react';
import { View, Text, TextInput, Button } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { NavigationProp } from '@/constants/Props';
import { client,xml  } from '@xmpp/client';




const Index: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigation = useNavigation<NavigationProp>();

  const handleLogin = () => {
    const domain = 'alumchat.lol'; 
    const host = 'alumchat.lol';
    const port = 5222;

    const xmpp = client({
      domain: domain,
      username: username,
      password: username,
    });
    xmpp.on("error", (err) => {
      navigation.navigate('homeTabs', {username: "Shit went bad"});
    });
    xmpp.start().catch(console.error);
    navigation.navigate('homeTabs', {username: username});
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text>Login</Text>
      <TextInput
        placeholder="Username"
        id="username"
        value={username}
        onChangeText={setUsername}
        style={{ height: 40, borderColor: 'gray', borderWidth: 1, marginBottom: 20, width: '80%' }}
      />
      <TextInput
        placeholder="Password"
        id="password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
        style={{ height: 40, borderColor: 'gray', borderWidth: 1, marginBottom: 20, width: '80%' }}
      />
      <Button title="Login" onPress={handleLogin} />
    </View>
  );
};

export default Index;
