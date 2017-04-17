(ns vquiz.db)

(def default-db
  {:quiz-title "Welcome to VQuiz"
   :quiz-intro "This is a quiz created by VQuiz. Click on start to begin the quiz!"
   :questions {
               :1 {:video-id "gXq-14lV79s"
                   :start-seconds 10
                   :end-seconds 20
                   :question "What is going to happen next?"}
               }})
