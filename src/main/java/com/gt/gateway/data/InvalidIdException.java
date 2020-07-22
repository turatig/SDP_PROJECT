package com.gt.gateway.data;

/*
Exception to indicate that a node tried to register with an already taken Id
 */
public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String msg){ super(msg); }
}
