package hep.crest.testutils;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.exceptions.ConflictException;

public class ExceptionsExample {

    public ExceptionsExample() {

    }

    public static void main(String[] args) {

        try {
            System.out.println("Send an Exception for IOV");
            throw new ConflictException("iov is already there");
        }
        catch (ConflictException e) {
            e.printStackTrace();
            System.out.println("Pojo Exception message : " + e.getMessage());
        }

        try {
            System.out.println("Send an Exception for IOV");
            throw new ConflictException("iov is already there");
        }
        catch (AbstractCdbServiceException e) {
            e.printStackTrace();
            System.out.println("Cdb Exception message : " + e.getMessage());
        }

    }
}
