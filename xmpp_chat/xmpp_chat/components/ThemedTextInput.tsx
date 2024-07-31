import { TextInput, type TextInputProps, StyleSheet } from 'react-native';

export type ThemedTextInput = TextInputProps & 
{ placeholder?: string;
 type?: 'default' | 'borderDefault' | 'borderHeavy';
 width?: number;
}

export function ThemedTextInput({placeholder,type='default',style}:ThemedTextInput) {
    return <TextInput
    style={[,
        type === 'default' ? styles.default : undefined,
        type === 'borderDefault' ? styles.borderDefault : undefined,
        type === 'borderHeavy' ? styles.borderHeavy : undefined,
        style,
    ]}
    placeholder={placeholder ? placeholder : ''}
    
    />
}

const styles = StyleSheet.create({ 
    default: {fontSize: 16},
    borderDefault : {fontSize: 16,borderWidth: 1, borderColor: '#0a7ea4',paddingLeft: 20,paddingRight: 20,paddingBottom:5,paddingTop: 5,borderRadius: 10},
    borderHeavy : {fontSize: 16,borderWidth: 3, borderColor: '#0a7ea4',paddingLeft: 20,paddingRight: 20,paddingBottom:5,paddingTop: 5,borderRadius: 10}
})