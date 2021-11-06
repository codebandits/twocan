import React from 'react'
import Typography from '@material-ui/core/Typography'
import {Page} from '../layout/page'
import {Logo} from '../logo'
import {Box, Button, Container} from '@material-ui/core'
import {useBirds} from "./bird";
import {useDialogState} from "../dialog";
import {AddBirdDialog} from "./add-bird-dialog";
import {isFetchGetResponseOk} from '../fetch/fetch-get'
import {BirdList} from './bird-list'

export const BirdsPage = () => {
    const {
        open: addBirdOpen,
        handleOpen: handleAddBirdOpen,
        handleClose: handleAddBirdClose,
    } = useDialogState()
    const {data: birdsResponse} = useBirds()

    if (birdsResponse === undefined) {
        return null
    } else if (isFetchGetResponseOk(birdsResponse)) {
        return (
            <>
                <Page title="Twocan">
                    <Container maxWidth="md">
                        <Box textAlign="center" mt={2}><Logo/></Box>
                        <Typography variant="h4" align="center">Birds</Typography>
                        <Box textAlign="right" my={2}>
                            <Button
                                color="primary"
                                variant="contained"
                                onClick={handleAddBirdOpen}>
                                Add
                            </Button>
                        </Box>
                        <BirdList birds={birdsResponse.data}/>
                    </Container>
                </Page>

                <AddBirdDialog open={addBirdOpen} handleClose={handleAddBirdClose}/>
            </>
        )
    } else {
        return null
    }
}
