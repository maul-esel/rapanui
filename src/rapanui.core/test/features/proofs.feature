Feature: Simple proofs

  Specify several simple proofs as cucumber scenarios

  Scenario: Prove R = R˘ for symmetric relations
    Given I have a proof environment with the rule system "symmetric_equality.raps"
    And I add the premise that R is symmetric

    When I start a new conclusion with start term R
    Then the equal term R˘˘ is suggested
    When I apply this suggestion
    Then the greater term R˘ is suggested

    When I apply this suggestion
    And I start a new conclusion with start term R
    Then the equal term R˘ is suggested

  Scenario: Prove that the converse of a linear order is also a linear order
    Given I have a proof environment with the rule system "converse_order.raps"
    And I add the premise that R is a partial order
    And I add the premise that R is linear

    # Reflexivity:

    When I start a new conclusion with start term I
    Then the equal term I˘ is suggested
    When I apply this suggestion
    Then the greater term R˘ is suggested

    # Transitivity:

    When I start a new conclusion with start term R˘;R˘
    Then the equal term (R;R)˘ is suggested
    When I apply this suggestion
    Then the greater term R˘ is suggested

    # Antisymmetry:

    When I start a new conclusion with start term R˘ ∩ R˘˘
    Then the equal term R˘ ∩ R is suggested
    When I apply this suggestion
    Then the equal term R ∩ R˘ is suggested
    When I apply this suggestion
    Then the greater term I is suggested

    # Linearity:

    When I start a new conclusion with start term R˘ ∪ R˘˘
    Then the equal term R˘ ∪ R is suggested
    When I apply this suggestion
    Then the equal term R ∪ R˘ is suggested
    When I apply this suggestion
    Then the equal term Π is suggested

  Scenario: Proof R = I if R is an equivalence relation and a partial order
    Given I have a proof environment with the rule system "equivalence_order.raps"
    And I add the premise that R is an equivalence relation
    And I add the premise that R is a partial order

    When I start a new conclusion with start term R
    Then the equal term R˘˘ is suggested
    When I apply this suggestion
    Then the greater term R˘ is suggested

    When I apply this suggestion
    And I start a new conclusion with start term R
    Then the equal term R ∩ R˘ is suggested
    When I apply this suggestion
    Then the greater term I is suggested

    When I apply this suggestion
    And I start a new conclusion with start term R
    Then the equal term I is suggested

  Scenario: Prove the inverse of a dense relation is also dense
    Given I have a proof environment with the rule system "dense_converse.raps"
    And I add the premise that R is dense

    When I start a new conclusion with start term R˘
    Then the greater term (R;R)˘ is suggested
    When I apply this suggestion
    Then the equal term R˘;R˘ is suggested

  Scenario: Prove a reflexive relation is dense
    Given I have a proof environment with the rule system "dense_reflexive.raps"
    And I add the premise that R is reflexive

    When I start a new conclusion with start term R
    Then the equal term I;R is suggested
    When I apply this suggestion
    Then the greater term R;R is suggested
