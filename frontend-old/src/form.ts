import {FormEventHandler, useCallback, useEffect, useMemo, useRef, useState} from 'react'
import {StandardTextFieldProps} from '@material-ui/core/TextField/TextField'
import {ButtonProps} from '@material-ui/core'
import {makeCancelable} from './promise'
import {
    FetchSubmitResponse,
    isFetchSubmitResponseBadRequestErrors,
    isFetchSubmitResponseSuccess
} from './fetch/fetch-submit'
import {RequestErrors} from "./fetch/fetch";

type ValueType = string | number | null

type ValuesType = {
    [key: string]: ValueType
}

type Config<Values extends ValuesType> = {
    initialValues: Values
    submit: (values: Values) => Promise<FetchSubmitResponse<Values>>
    onSuccessCallback?: () => void
}

type Field<ValueType> = {
    onChange: (value: ValueType) => void
    textFieldProps: {
        FormHelperTextProps: StandardTextFieldProps['FormHelperTextProps']
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

export const useFormData = <Values extends ValuesType>(config: Config<Values>): FormData<Values> => {
    const {submit, initialValues, onSuccessCallback} = config
    useLogErrorWhenUpdate(submit, 'The submit configuration of useFormData changed. This is a bug.')
    useLogErrorWhenUpdate(initialValues, 'The initialValues configuration of useFormData changed. This is a bug.')
    const [values, setValues] = useState<Values>(initialValues)
    const [errors, setErrors] = useState<RequestErrors<Values>>({})
    const [submitting, setSubmitting] = useState(false)

    const onSubmit: FormEventHandler<HTMLFormElement> = useCallback(event => {
        event.preventDefault()
        setSubmitting(true)
    }, [])

    useEffect(() => {
        if (submitting) {
            setErrors({})
            const cancelable = makeCancelable(submit(values))
            cancelable.promise
                .then(response => {
                    setSubmitting(false)
                    if (isFetchSubmitResponseSuccess(response)) {
                        setValues(initialValues)
                        onSuccessCallback?.()
                    } else if (isFetchSubmitResponseBadRequestErrors(response)) {
                        setErrors(response.errors)
                    }
                    return response
                })
                .catch(() => setSubmitting(false))
            return () => {
                cancelable.cancel()
                setSubmitting(false)
            }
        }
    }, [initialValues, onSuccessCallback, submit, submitting, values])

    const fields: Fields<Values> = useMemo(() => {
        return Object.entries(values)
            .reduce<Partial<Fields<Values>>>((acc, [key, value]) => {
                const onChange = (newValue: typeof value) => {
                    if (!submitting) {
                        setValues(prev => ({...prev, [key]: newValue}))
                    }
                }
                return {
                    ...acc,
                    [key]: {
                        onChange: onChange,
                        textFieldProps: {
                            onChange: event => onChange(event.target.value),
                            FormHelperTextProps: {
                                'data-testid': `${key}-helper-text`,
                            },
                            error: errors[key] !== undefined,
                            helperText: errors[key] || '\u200b',
                            value: value,
                        },
                    },
                }
            }, {}) as Fields<Values>
    }, [errors, submitting, values])

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

const useLogErrorWhenUpdate = (value: any, message: string) => {
    const messageRef = useRef(message)
    const valueRef = useRef(value)
    useEffect(() => {
        if (process.env.NODE_ENV !== 'production') {
            if (valueRef.current !== value) {
                console.error(messageRef.current)
            }
        }
    }, [value])
    useEffect(() => {
        valueRef.current = value
    })
}
