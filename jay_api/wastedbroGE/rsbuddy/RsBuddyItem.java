package scripts.jay_api.wastedbroGE.rsbuddy;

import com.allatori.annotations.DoNotRename;

/**
 * Model that represents an item from the RSBuddy API
 */
@DoNotRename
public class RsBuddyItem
{
    @DoNotRename
    private int id, overall_average, sell_average, sp, buy_average;
    @DoNotRename
    private boolean members;
    @DoNotRename
    private String name;

    @Override
    public String toString()
    {
        return "Id: " + id + "\n" +
                "Name: " + name + "\n" +
                "Average Price: " + overall_average + "\n" +
                "Average Sell Price: " + sell_average + "\n" +
                "Average Buy Price: " + buy_average + "\n" +
                "Is Member's Item? " + members + "\n" +
                "sp(?): " + sp;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getAveragePrice()
    {
        return overall_average;
    }

    public void setAveragePrice(int overall_average)
    {
        this.overall_average = overall_average;
    }

    public int getAverageSellPrice()
    {
        return sell_average;
    }

    public void setAverageSellPrice(int sell_average)
    {
        this.sell_average = sell_average;
    }

    public int getSp()
    {
        return sp;
    }

    public void setSp(int sp)
    {
        this.sp = sp;
    }

    public int getAverageBuyPrice()
    {
        return buy_average;
    }

    public void setAverageBuyPrice(int buy_average)
    {
        this.buy_average = buy_average;
    }

    public boolean isMembers()
    {
        return members;
    }

    public void setMembers(boolean members)
    {
        this.members = members;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


}

/*
{"id": {
    "overall_average",
    "sell_average",
    "name",
    "id",
    "sp",
    "members",
    "buy_average"}}
 */
