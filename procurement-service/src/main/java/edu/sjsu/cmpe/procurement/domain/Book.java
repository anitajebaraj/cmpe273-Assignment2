package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;




public class Book {
    private long isbn;
    private String title;
    private String responseFromGet;
    private String isbnList;
    private static ArrayList orderIsbnList=new ArrayList();;
    // add more fields here


	public ArrayList getOrderIsbnList() {
		return orderIsbnList;
	}

	public void setOrderIsbnList(ArrayList orderIsbnList) {
		this.orderIsbnList = orderIsbnList;
	}

	public String getIsbnList() {
		return isbnList;
	}

	public void setIsbnList(String isbnList) {
		this.isbnList = isbnList;
	}

	public String getResponseFromGet() {
		return responseFromGet;
	}

public void setResponseFromGet(String responseFromGet) {
	this.responseFromGet = responseFromGet;
}

	/**
     * @return the isbn
     */
    public long getIsbn() {
	return isbn;
    }

    /**
     * @param isbn
     *            the isbn to set
     */
    public void setIsbn(long isbn) {
	this.isbn = isbn;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
	this.title = title;
    }
}
