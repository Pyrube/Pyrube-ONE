# Pyrube-ONE
  Pyrube-ONE is a framework of JAVA-based APIs. It provides general APIs and makes programming easier, such as: logging, application/business configuration, application cache, i18n, date/number/amount(currency-based) format, login user holder, timer task/job, application lifecycle management, and some utilities (crypto, arith, xml, etc).  
# Bind All in ONE
  While you coding, there are so many JARs, JAVA packages and classes from third-parties. Perhaps, Pyrube-ONE can help you out of this with one class: Apps. You can use or extend it to bind all of these classes you are using into ONE. e.g. private static Logger logger = Apps.a.logger.named(className), instead of private static Logger logger = Logger.getInstance(className);  
# More Nature Language
  At the same time, Pyrube-ONE with Apps is designed with more nature language to enhance your coding mode, like Apps.a.note(String content).of(String type, String ID, String status).in.event(String event).to.leave(), instead of Note note = new Note(); note.setContent(content); note setType(type); serviceBean.leave(note);
