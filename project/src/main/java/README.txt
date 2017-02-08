Try to separate ImageJ-dependent from other code. Keep IJ-plugins short.
Always use double instead of float (except for large arrays).
There is no sense to wrap individual methods into objects that have no instance variables (eg. SegmentGraph)
Class RGB is unnecessary, use Color.
Clean up 'diff(Color myCol1, Color myCol2)'
Mechanism for passing/setting parameters of Segmenter should be reconsidered.
Check class/variable scope (reduce visibility from public if possible)!
