declare module 'react-native-xmpp' {
    export function trustHosts(hosts: string[]): void;
    export function connect(
        username: string,
        password: string,
        domain: string,
        host: string,
        port: number
    ): void;
    export function disconnect(): void;
    export function on(
        event: 'login' | 'error' | 'disconnect' | 'message',
        callback: (message?: any) => void
    ): void;
    export function sendMessage(text: string, to: string, thread: string): void;
    export function fetchRoster(): void;
    export function sendPresence(to: string, type: string): void;
}
