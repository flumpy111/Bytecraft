package info.bytecraft.api;

import java.util.Date;

public class PlayerReport
{
    
    public enum Action{
        KICK, SOFTWARN, HARDWARN, BAN, MUTE, COMMENT;
        
        public static Action fromString(String str)
        {
            for (Action action : Action.values()) {
                if (str.equalsIgnoreCase(action.toString())) {
                    return action;
                }
            }

            return null;
        }
    };
    
    public enum ReportTime{
        MINUTES(60L), 
        HOURS(60 * 60L), 
        DAYS(60 * 60 * 24L), 
        WEEKS(60 * 60 * 24 * 7L), 
        MONTHS(60 * 60 * 24 * 30L);
        
        private final long time;
        
        private ReportTime(long time)
        {
            this.time = time;
        }

        public long getTime()
        {
            return time;
        }
        
        public static ReportTime byString(String args)
        {
            if(args.startsWith("minute"))return MINUTES;
            if(args.startsWith("hour"))return HOURS;
            if(args.startsWith("day"))return DAYS;
            if(args.startsWith("week"))return WEEKS;
            if(args.startsWith("month"))return MONTHS;
            return null;
        }
    };
    
    private int id = 0;
    private String subjectName;
    private String issuerName;
    private Action action = null;
    private String message = "";
    private Date timestamp = new Date();
    private Date validUntil = null;
    
    public PlayerReport()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int v)
    {
        this.id = v;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String v)
    {
        this.subjectName = v;
    }

    public String getIssuerName()
    {
        return issuerName;
    }

    public void setIssuerName(String v)
    {
        this.issuerName = v;
    }

    public Action getAction()
    {
        return action;
    }

    public void setAction(Action v)
    {
        this.action = v;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String v)
    {
        this.message = v;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date v)
    {
        this.timestamp = v;
    }

    public Date getValidUntil()
    {
        return validUntil;
    }

    public void setValidUntil(Date v)
    {
        this.validUntil = v;
    }
}
