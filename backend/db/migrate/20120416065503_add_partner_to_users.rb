class AddPartnerToUsers < ActiveRecord::Migration
  def self.up
    add_column :users, :partner, :integer, :default => 0
  end

  def self.down
    remove_column :users, :partner
  end
end
