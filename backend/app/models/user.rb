class User < ActiveRecord::Base
  has_one :location, :dependent => :delete
end
