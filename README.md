### The Task:
Create a basic job system that can schedule and execute jobs of different types.
The users of this system will be another developers and their main objective is to create
different job definitions which they can run asynchronously.
- Important implementation notes:
Write tests, Even if you have never written a single test before give it a try and see how it
goes. We are strong believers in tests so you should be comfortable with it :)
- For simplicity's sake the system can run in-memory. A persistent store is not required but do
design the system in a way that allows switching stores later on.
### Requirements:
- Language: Java 8+
### Jobs Definitions:
- The system should support custom job types. The different job types will be defined
by the developers who use this system.
- A job should have a life-cycle that can be tracked i.e. we need to track the job's
current state - running, failed etc.
Scheduling:
- Jobs can be scheduled for periodic execution (every 1, 2, 6, or 12 hours).
- Jobs can be executed immediately for a one time run.
Concurrency:
- Jobs need to run concurrently. A job should not wait for a previous job to finish
before it can run. You can assume that there are no dependencies between any two
jobs.
- There should be a limit on the amount of jobs that can run concurrently at any given
moment. If the limit is reached the pending job(s) should wait for an open spot.
Submitting Your Solution: Upload your solution to a git repository. Use GitHub, GitLab,
Bitbucket or similar.
### Bonus points:
You can pick any or none of the following to implement.
- Uniqueness - define a unique identifier for each job. Two jobs with the same unique
identifier and the same type cannot run concurrently. A Job can run concurrently with other
jobs of a different type.
- Cancellations - have a mechanism that notifies jobs they need to abort and clean up.
- Introspection - fetch current system stats e.g. all running jobs, size of queue, etc