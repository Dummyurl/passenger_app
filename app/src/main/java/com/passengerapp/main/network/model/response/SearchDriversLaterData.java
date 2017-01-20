package com.passengerapp.main.network.model.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by adventis on 8/6/15.
 */
public class SearchDriversLaterData implements Serializable {
    public String CategoryName;
    public int CategoryID;
    public int CompanyID;
    public String[] CompaniesName;
    public float Fare;
    public float HourlyRate;
    public float NumOfHours;
    public String StyleCategoryImage;
    public List<SearchDriversData> Vehicles;
    public int numberOfPass;
    public int NumberOfSuitcases;
}
