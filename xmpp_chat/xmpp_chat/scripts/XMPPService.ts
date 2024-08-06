// xmppService.ts
import XMPP from 'react-native-xmpp';

class XMPPService {
  private static instance: XMPPService;
  
  private constructor() {
    // Configure the XMPP connection
    XMPP.on('login', this.onLogin);
    XMPP.on('error', this.onError);
    XMPP.on('disconnect', this.onDisconnect);
    XMPP.on('message', this.onMessage);
  }

  static getInstance() {
    if (!this.instance) {
      this.instance = new XMPPService();
    }
    return this.instance;
  }

  connect(username: string, password: string, domain: string, host: string, port: number) {
    XMPP.trustHosts([host]);
    XMPP.connect(username, password, domain, host, port);
  }

  disconnect() {
    XMPP.disconnect();
  }

  private onLogin() {
    console.log('Logged in!');
  }

  private onError(error: any) {
    console.log('Error: ', error);
  }

  private onDisconnect(error: any) {
    console.log('Disconnected: ', error);
  }

  private onMessage(message: any) {
    console.log('Message: ', message);
  }
}

export default XMPPService.getInstance();
