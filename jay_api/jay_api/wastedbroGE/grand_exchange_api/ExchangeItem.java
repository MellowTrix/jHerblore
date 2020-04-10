package scripts.jay_api.wastedbroGE.grand_exchange_api;

import com.allatori.annotations.DoNotRename;

/**
 *
 */
@DoNotRename
public class ExchangeItem
{
    @DoNotRename
    private int id;

    @DoNotRename
    private String icon, icon_large, type, typeIcon, name, description, members;

    @DoNotRename
    private PriceTrend current, today;

    @DoNotRename
    private TrendChange day30, day90, day180;

    @Override
    public String toString()
    {
        return "ExchangeItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", members='" + members + '\'' +
                ", price='" + current.price + '\'' +
                '}';
    }

    public int getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public int getPrice()
    {
        return current.price;
    }

    public String getTrend30Days()
    {
        return day30.change;
    }

    public String getTrend90Days()
    {
        return day90.change;
    }

    public String getTrend180Days()
    {
        return day180.change;
    }

    public String getIconUrl()
    {
        return icon;
    }

    public String getIcon_largeUrl()
    {
        return icon_large;
    }

    public String getName()
    {
        return name;
    }

    public boolean isMembers()
    {
        return Boolean.parseBoolean(members);
    }
}

