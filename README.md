# TrackView

This sample project demonstrates the use of path data to draw custom views at
runtime. These objects can then have their path data extracted and manipulated
to support things like complex animations.

A sample usage would be to replace the code in the TrackView::drawTrack(),
with a path drawing loop which is iterating over a collection of GPS coordinates
to draw a hiking trail or driving path.
