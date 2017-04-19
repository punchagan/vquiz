(ns vquiz.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :quiz-title
 (fn [db]
   (:quiz-title db)))

(re-frame/reg-sub
 :quiz-intro
 (fn [db]
   (:quiz-intro db)))

(re-frame/reg-sub
 :quiz-progress
 (fn [db]
   (:quiz-progress db)))

(re-frame/reg-sub
 :quiz-started
 (fn [db]
   (:quiz-started db)))

(re-frame/reg-sub
 :questions
 (fn [db]
   (:questions db)))

(re-frame/reg-sub
 :display-question
 (fn [db]
   (:display-question db)))

(re-frame/reg-sub
 :answer-correct?
 (fn [db]
   ( :answer-correct? db)))

(re-frame/reg-sub
 :video-ended?
 (fn [db]
   ( :video-ended? db)))
