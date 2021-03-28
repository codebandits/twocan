import {Box, Button, Card, CardContent, TextField, Typography} from '@material-ui/core'
import {Page} from '../layout/page'
import {PageContent} from '../layout/page-content'
import React, {useCallback} from 'react'
import {useFormData} from '../form'
import {useQueryClient} from 'react-query'
import {Logo} from "../logo";

export const LoginPage = () => {

    const queryClient = useQueryClient()
    const login = useCallback((data: { emailAddress: string }) =>
        fetch('/api/login', {
            method: 'post',
            body: JSON.stringify(data)
        }).then(() => queryClient.invalidateQueries('session')), [queryClient])
    const formData = useFormData({
        initialValues: {emailAddress: ''},
        submit: login,
    })

    return (
        <Page title="Twocan">
            <PageContent>
                <Box display="flex" justifyContent="center">
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
            </PageContent>
        </Page>
    )
}
