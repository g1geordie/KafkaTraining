#### protocol practice .

### no-blocking  (prevent wait for IO time )
## block
>> accept -> read socket -> wait network 

## no-block
>> accept --(wait network)-> read socket 

### async  (prevent too many thread)
## sync 
>> accept ->new thread ->doIt 

## async 
>> accept -> queue  , threadpool->doit