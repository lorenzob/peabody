peabody - back in time logging
=======

Peabody is an extension for some logging frameworks to allow *back in time* logging.

Usually you keep your logger to the INFO level, but as soon as something goes wrong 
you need to raise the log level to DEBUG just...*before* something went wrong.

Peabody does exactly this: detects an error, goes back in time and rise the log 
level for you.

Behind the scenes the trick is simple: it keeps a small buffer of recently logged messages
that were below the current threshold. As soon as a message is logged with level ERROR
or higher the whole buffer is written to the log file.

Here's a real world example. Without peabody:

> 2013-05-10 17:25:49,717 | INFO  |  BEGIN fetchTokenByMessageId  
2013-05-10 17:25:55,999 | ERROR |  Ricerca fallita. msgId: '<654915668.1334672807.1368001634734liaspec01@zzz.it>' - LD0999 - Error - it.zzz.messages.ricercaDocumento.result.Eccezione@24eecb5c  

With peabody (in bold infos otherwise lost):

> 2013-05-10 **17:25:49,717** | INFO  |  BEGIN fetchTokenByMessageId  
2013-05-10 **17:25:55,998** | ERROR |  **>>>>>>>>> FLASHBACK START >>>>>>>>>**  
2013-05-10 **17:25:49,717** | INFO  |  BEGIN fetchTokenByMessageId  
2013-05-10 17:25:49,718 | **DEBUG** |  Start unmarshaling  
2013-05-10 17:25:49,719 | DEBUG |  Completed unmarshaling  
2013-05-10 17:25:49,719 | DEBUG |  Reading kdv doc **5666673**  
2013-05-10 17:25:49,818 | DEBUG |  messageId '<654915668.1334672807.1368001634734liaspec01@zzz.it>'  
2013-05-10 17:25:49,818 | DEBUG |  Ref token **null**  
2013-05-10 17:25:49,818 | DEBUG |  Read legaldoc document for messageID  '<654915668.1334672807.1368001634734liaspec01@zzz.it>', classeDocumentale: '**zzz-mail-search**'  
2013-05-10 17:25:55,999 | ERROR |  **<<<<<<<<<  FLASHBACK END  <<<<<<<<<**  
2013-05-10 17:25:55,999 | **ERROR** |  Ricerca fallita. msgId:  '<654915668.1334672807.1368001634734liaspec01@zzz.it>' - LD0999 - Error - it.zzz.messages.ricercaDocumento.result.Eccezione@24eecb5c  

## Supported frameworks:

- log4j through a custom LoggerFactory. Just declare a custom categoryFactory in your log4j config file. See src/test/resources for examples.

### Config properties (default value)

* triggerLevel: which level triggers the "flashback" (ERROR)
* bufferSize: how many messages to keep (100)
* loggerList: csv list of logger name *prefixes* that should be included (include all)
* discardOldEventsThreshold: on flashback do not log messages older than this time (millis, 10000)
* collectLocationInformation: enable the use of %C, %M, %L in PatternLayout for recorded messages (true)
* keepLoggedEvents: flashback includes lines already logged for better readability (true)

### Performance

todo

