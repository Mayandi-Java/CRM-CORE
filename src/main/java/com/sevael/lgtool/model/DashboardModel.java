package com.sevael.lgtool.model;

import java.util.List;

public class DashboardModel {

	private int wondeals;
	private double wondealsworth;
	private int dealsdiff;
	private int newcontacts;
	private int totalcontacts;
	private int contactdiff;
	private int newleads;
	private int converted;
	private int leaddiff;
	private List<DashboardActivityModel> activities;

	public int getWondeals() {
		return wondeals;
	}

	public void setWondeals(int wondeals) {
		this.wondeals = wondeals;
	}

	public double getWondealsworth() {
		return wondealsworth;
	}

	public void setWondealsworth(double wondealsworth) {
		this.wondealsworth = wondealsworth;
	}

	public int getDealsdiff() {
		return dealsdiff;
	}

	public void setDealsdiff(int dealsdiff) {
		this.dealsdiff = dealsdiff;
	}

	public int getNewcontacts() {
		return newcontacts;
	}

	public void setNewcontacts(int newcontacts) {
		this.newcontacts = newcontacts;
	}

	public int getTotalcontacts() {
		return totalcontacts;
	}

	public void setTotalcontacts(int totalcontacts) {
		this.totalcontacts = totalcontacts;
	}

	public int getContactdiff() {
		return contactdiff;
	}

	public void setContactdiff(int contactdiff) {
		this.contactdiff = contactdiff;
	}

	public List<DashboardActivityModel> getActivities() {
		return activities;
	}

	public void setActivities(List<DashboardActivityModel> activities) {
		this.activities = activities;
	}

	public int getNewleads() {
		return newleads;
	}

	public void setNewleads(int newleads) {
		this.newleads = newleads;
	}

	public int getConverted() {
		return converted;
	}

	public void setConverted(int converted) {
		this.converted = converted;
	}

	public int getLeaddiff() {
		return leaddiff;
	}

	public void setLeaddiff(int leaddiff) {
		this.leaddiff = leaddiff;
	}

}
