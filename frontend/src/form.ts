import {FormEventHandler, useCallback, useEffect, useMemo, useState} from 'react'
import {StandardTextFieldProps} from '@material-ui/core/TextField/TextField'
import {ButtonProps} from '@material-ui/core'
import {makeCancelable} from './promise'

type ValueType = string | number | null

type ValuesType = {
    [key: string]: ValueType
}

type Config<Values extends ValuesType, Result> = {
    initialValues: Values
    submit: (values: Values) => Promise<Result>
}

type Field<ValueType> = {
    onChange: (value: ValueType) => void
    textFieldProps: {
        helperText: StandardTextFieldProps['helperText']
        onChange: StandardTextFieldProps['onChange']
        value: StandardTextFieldProps['value']
    }
}

type Fields<Values extends ValuesType> = {
    [Key in keyof Values]: Field<Values[Key]>
}

type FormData<Values extends ValuesType> = {
    values: Values
    fields: Fields<Values>
    submitButtonProps: ButtonProps
    onSubmit: FormEventHandler<HTMLFormElement>
}

export const useFormData = <Values extends ValuesType, Result>({submit, initialValues}: Config<Values, Result>): FormData<Values> => {
    const [values, setValues] = useState<Values>(initialValues)
    const [submitting, setSubmitting] = useState(false)

    const onSubmit: FormEventHandler<HTMLFormElement> = useCallback(event => {
        event.preventDefault()
        setSubmitting(true)
    }, [])

    useEffect(() => {
        if (submitting) {
            const cancelable = makeCancelable(submit(values))
            cancelable.promise
                .then(() => setSubmitting(false))
                .catch(() => setSubmitting(false))
            return () => {
                cancelable.cancel()
                setSubmitting(false)
            }
        }
    }, [submit, submitting, values])

    const fields: Fields<Values> = useMemo(() => {
        return Object.entries(values)
            .reduce<Partial<Fields<Values>>>((acc, [key, value]) => {
                const onChange = (newValue: typeof value) => setValues(prev => ({...prev, [key]: newValue}))
                return {
                    ...acc,
                    [key]: {
                        onChange: onChange,
                        textFieldProps: {
                            onChange: event => onChange(event.target.value),
                            helperText: '\u200b',
                            value: value,
                        },
                    },
                }
            }, {}) as Fields<Values>
    }, [values])

    const submitButtonProps: ButtonProps = useMemo(() => {
        return {
            type: 'submit',
            disabled: submitting,
        }
    }, [submitting])

    return {
        values: values,
        fields: fields,
        onSubmit: onSubmit,
        submitButtonProps: submitButtonProps,
    }
}
