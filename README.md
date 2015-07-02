# jvision

JoakimVision - Monitoring application for the living room. Intended for use
with RPI2 (or similar non-touch devices) running Android.

The application will display a set of tiles containing useful (or interesting)
data. The tiles are randomly placed and scaled at frequent intervals. The
random placement will (hopefully) give a more dynamic feel to the system, and
(hopefully) prevent burn-ins. Various screen-savers are also planned to prevent
burns.


## Planned Features ##

None of the following features are, unless explicitly marked with an X or
otherwise stated, implemented to any degree.

Planned tiles, in semi-prioritized order:


    [X] Time and date
    [X] Public transport departures (Ruter, Oslo)
    [ ] Hacker-news feed
    [ ] External server status (uptime, online, etc)
    [ ] XKCD
    [ ] Arbitrary RSS feed
    [ ] Twitter feed
    [ ] Reddit feed
    [ ] Release status for series (i.e., when does the next episode come out)
    [ ] Google calendar feed
    [ ] Google Keep (or similar)
    [ ] Wunderlist


The wide array of various application keys and whatnot that would be required
with the current planned feature set, it is very unlikely that the application
will be useable by anyone without (at the very least) a lot of tinkering.

If the project gains traction, JVision will also get a control application for
tile configuration and prioritization.

All tiles will have a prioritization. The priority may depend on data
availability, time of day, who is home (see below), and external input. The
currently planned external input is Makey-Makey buttons which will bump the
priority of a certain tile for a brief period. How well Android handles
physical keyboards may be a limitation in this, however.

The current (long term) plan is that, given static IPs assigned to the
residents phones, JVision reacts to *who* is currently home. When noone is
home, the screen may turn off, or a screen-saver may be shown. When a subset of
the registered devices are present, certain tiles may be prioritized or
disabled all together. This would require assigning "ownership" to certain
tiles - my S.O. may for instance not be as interested in knowing what's going
on over at HN.
