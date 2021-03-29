import {Box, Button, Card, CardContent, Container, TextField, Typography} from '@material-ui/core'
import {Page} from '../layout/page'
import React, {useCallback} from 'react'
import {useFormData} from '../form'
import {useQueryClient} from 'react-query'
import {Logo} from "../logo";

const initialValues = {emailAddress: ''}

export const LoginPage = () => {

    const queryClient = useQueryClient()
    const login = useCallback((data: { emailAddress: string }) =>
        fetch('/api/login', {
            method: 'post',
            body: JSON.stringify(data)
        }).then(() => queryClient.invalidateQueries('session')), [queryClient])
    const formData = useFormData({
        initialValues: initialValues,
        submit: login,
    })

    return (
        <Page title="Twocan">
            <Container>
                <Box display="flex" justifyContent="center" mt={2}>
                    <Card>
                        <CardContent>
                            <Typography variant="h4" align="center">Login</Typography>
                            <Box textAlign="center"><Logo/></Box>
                            <form onSubmit={formData.onSubmit}>
                                <TextField
                                    {...formData.fields.emailAddress.textFieldProps}
                                    id="login-email-address-input"
                                    label="Email Address"
                                    fullWidth/>
                                <Button
                                    {...formData.submitButtonProps}
                                    size="large"
                                    color="primary"
                                    variant="contained"
                                    fullWidth>
                                    Login
                                </Button>
                            </form>
                        </CardContent>
                    </Card>
                </Box>
            </Container>
        </Page>
    )
}
