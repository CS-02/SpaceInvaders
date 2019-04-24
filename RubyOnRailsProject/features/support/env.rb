require 'capybara'
require 'capybara/cucumber'
require 'capybara/poltergeist'

Capybara.configure do |config|
  config.default_driver = :poltergeist
  config.app_host   = 'http://www.google.com'
end

Capybara.register_driver :poltergeist do |app|
	option = Hash.new
  option[:phantomjs] = 'D:\Descargas\phantomjs-2.1.1-windows\phantomjs-2.1.1-windows\bin\phantomjs.exe'
  Capybara::Poltergeist::Driver.new(app, option)
end

World(Capybara)
