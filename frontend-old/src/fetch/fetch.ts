export type Request = {
    [key: string]: string | number | boolean | null
}

export type RequestErrors<T extends Request> = {
    [key in keyof T]?: string
}

export type ResponseOk<T> = {
    status: 'OK'
    data: T
}

export type ResponseAccepted = {
    status: 'ACCEPTED'
}

export type ResponseCreated = {
    status: 'CREATED'
    id: string
}

export type ResponseBadRequestErrors<T extends Request> = {
    status: 'BAD_REQUEST'
    errors: RequestErrors<T>
}

export type ResponseBadRequestMessage = {
    status: 'BAD_REQUEST'
    message: string
}

export type ResponseNotFound = {
    status: 'NOT_FOUND'
    message: string
}

export type ResponseUnauthorized = {
    status: 'UNAUTHORIZED'
    message: string
}

export type ResponseForbidden = {
    status: 'FORBIDDEN'
    message: string
}

export type ResponseInternalServerError = {
    status: 'INTERNAL_SERVER_ERROR'
    message: string
}
