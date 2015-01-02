create constraint on (s:Sequence) assert s.name is unique;

create (n:Sequence { name: 'sermon-series', next: 100, increment: 10 });

create (n:Sequence { name: 'sermon', next: 100, increment: 10 });
