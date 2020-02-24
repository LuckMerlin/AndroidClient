package com.merlin.player;

public interface Status {;
 int STATUS_UNKNOWN = 2001;
 int STATUS_PAUSE = 2002;
 int  STATUS_IDLE= 2003;
 int  STATUS_WAITING= 2004;
 int  STATUS_STOP= 2005;
 int  STATUS_PROGRESS= 2006;
 int  STATUS_FINISH = 2007;
 int  STATUS_FINISH_ERROR= 2008;
 int  STATUS_SEEK= 2009;
 int  STATUS_RESTART= 2011;
 int  STATUS_CACHING= 2012;
 int  STATUS_CACHE_FINISH= 2013;
 int  STATUS_PREPARING= 2014;
 int  STATUS_OPEN_FAIL= 2015;
 int  STATUS_OPENING= 2016;
 int  STATUS_START= 2017;
 int  STATUS_OPENED= 2018;
 int  STATUS_PLAYING= 2019;
 int  STATUS_MODE_CHANGED= 2020;

}
