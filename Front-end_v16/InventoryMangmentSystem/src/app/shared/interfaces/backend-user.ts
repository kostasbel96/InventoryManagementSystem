export interface User {
    firstname: string,
    lastname: string,
    role: string,
    username: string,
    password: string
}

export interface Credentials {
    username: string,
    password: string
}

export interface LoggedInUser {
    fullname: string,
    username: string
}