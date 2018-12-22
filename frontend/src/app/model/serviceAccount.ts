export interface ServiceAccount {
}

export class UserServiceAccount implements ServiceAccount {
    username: string;
    password: string;
}

export class KeyServiceAccount implements ServiceAccount {
    sshKey: string;
}
