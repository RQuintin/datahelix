# Copyright 2019 Scott Logic Ltd
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
Feature: User can specify that data must be created to conform to each of multiple specified constraints.

  Background:
    Given the generation strategy is full

  Scenario: Running an 'allOf' request that contains a valid nested allOf request should be successful
    Given there is a non nullable field foo
    And All Of the next 2 constraints
    And All Of the next 2 constraints
    And foo is matching regex /[a-b]{2}/
    And foo is of length 2
    And foo is shorter than 3
    And foo has type "string"
    Then the following data should be generated:
      | foo  |
      | "aa" |
      | "ab" |
      | "bb" |
      | "ba" |

  Scenario: Running an 'allOf' request that contains an invalid nested allOf request should generate null
    Given there is a non nullable field foo
    And foo has type "string"
    And All Of the next 2 constraints
    And All Of the next 2 constraints
    And foo is matching regex /[a-k]{3}/
    And foo is matching regex /[1-5]{3}/
    And foo is longer than 4
    Then the following data should be generated:
      | foo |

  Scenario: Running a 'allOf' request that includes multiple values within the same statement should be successful
    Given there is a non nullable field foo
    And foo has type "string"
    And All Of the next 2 constraints
    And foo is equal to "Test01"
    And foo is equal to "Test01"
    And foo is anything but null
    Then the following data should be generated:
      | foo      |
      | "Test01" |

  Scenario: User attempts to combine two constraints that only intersect at the empty set within an allOf operator should not generate data
    Given there is a non nullable field foo
    And foo has type "string"
    And All Of the next 2 constraints
    And foo is equal to "Test01"
    And foo is equal to "5"
    And foo is anything but null
    Then no data is created
