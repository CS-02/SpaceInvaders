Given /^I am on the home page$/ do
  visit('http://www.youtube.com')
end

When (/^I search for "(.*?)"$/) do |term|
  options = Hash.new
  fill_in('search_query', with: term)
  find('.search-form').send_keys(:return)
end

Then (/^I should see "([^"]*)"$/) do |text|
  expect have_content(text)
end

Given ("I am on the search page of {string}") do |term|
  visit('http://www.youtube.com')
  fill_in('search_query', with: term)
  find('.search-form').send_keys(:return)
end

When("I click on the first video") do
  click_link('An Introduction to Behavior-Driven Development (BDD) with Cucumber for Java')
end

Then("I should be taken to that video") do
  expect(page).to have_current_path("/watch?v=MCaXumfckmQ") 
end
  
Given("I am on a video page") do
  visit('http://www.youtube.com/watch?v=MCaXumfckmQ')
end

When("I click on subscribe button") do
  find(:xpath,"//*[text()='#{'Suscribirse'}']").click
end

Then("the log in window appears") do
  expect(page).to have_text('Iniciar sesión')
end


Then("the share window appears") do
  expect(page).to have_text('Insertar')
end

When("I click on {string} button") do |term|
  click_on(term)
end



