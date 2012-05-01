class UsersController < ApplicationController

  def reset
    user = User.find_by_id(params[:id])
    response = 0
    
    if !user.nil?
      user.partner = 0
      user.save
      response = 1
    end
  
    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def accept
    user1 = User.find_by_id(params[:one])
    user2 = User.find_by_id(params[:two])
    loc1 = Location.find_by_user_id(params[:one])
    loc2 = Location.find_by_user_id(params[:two])
    response = 0

    if !user1.nil? and !loc1.nil? and !user2.nil? and !loc2.nil?
      user1.partner = -2 
      user2.partner = -2
      user1.save 
      user2.save
      response = 1
    end
   
    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def reject
    user1 = User.find_by_id(params[:one])
    user2 = User.find_by_id(params[:two])
    loc1 = Location.find_by_user_id(params[:one])
    loc2 = Location.find_by_user_id(params[:two])
    response = 0

    if !user1.nil? and !loc1.nil? and !user2.nil? and !loc2.nil?
      user1.partner = 0 
      user2.partner = 0 
      user1.save 
      user2.save
      response = 1
    end
   
    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def partner
    user = User.find_by_id(params[:id])
    response = 0 

    if !user.nil? and user.active == 1 and user.partner != 0 
      response = user.partner
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def changeinfo
    user = User.find_by_id(params[:id])
    response = 0 

    if !user.nil?
      user.name = params[:name]
      user.year = params[:year]
      user.save
      response = 1
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def login
    user = User.find_by_username(params[:username])
    response = 0 
   
    if !user.nil? and user.active == 0 and user.password == params[:password] 
       user.active = 1 
       user.save
       response = 1 # success
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def logout
    user = User.find_by_username(params[:username])
    response = 0 

    if !user.nil? and user.active == 1
        user.active = 0
        user.location.destroy
        partner = user.partner 
        user.partner = 0 
        otherUser = User.find_by_id(partner)
        otherUser.partner = 0
        otherUser.save
        user.save
        response = 1 # succesful log out 
    end
  
    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def getuser
    user = User.find_by_id(params[:id])
    response = 0
 
    if !user.nil?
      response = user
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def getloc
    location = Location.find_by_user_id(params[:id])
    response = 0 
 
    if !loc.nil?
      response = location
    end

    respond_to do |format|
      format.json { render :json => location }
    end
  end

  def sendloc
    user = User.find_by_id(params[:id])
    response = 0 

    if !user.nil? and user.active == 1
      loc = user.location
      lat = Float(params[:lat])
      long = Float(params[:long])
 
      if loc.nil?
        loc = Location.new(:lat => lat, :long => long)
        loc.user_id = user.id
        user.location = loc
        user.save
        response = 1
      else
        Location.destroy(loc.id)
        newLoc = Location.new(:lat => lat, :long => long)
        newLoc.user_id = user.id
        user.location = newLoc
        user.save
        response = 1
      end
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def closestMatch
    user = User.find_by_id(params[:id])
    response = 0
    #response = 'hello' 

    if !user.nil? and user.active == 1 
      #response = "blah"
      loc = Location.find_by_user_id(params[:id])

      minLoc = 0 
      if !loc.nil?
        #response = "blah3"
        activeUsers = User.where("active = 1 AND partner = 0")
        locs = activeUsers.collect{ |user| user.location }
        locs.each { |x| 
          if x != nil and x.user_id != loc.user_id
            if minLoc == 0 
              minLoc = x
            elsif Math.sqrt((loc.lat - x.lat)**2 + (loc.long - x.long)**2)  < Math.sqrt((loc.lat - minLoc.lat)**2 + (loc.long - minLoc.long)**2) 
              minLoc = x
            end
          end
        }

        #response = 'bye'
        if minLoc != 0
          #response = 'hola'
          other = User.find_by_id(minLoc.user_id) 
          other.partner = user.id
          user.partner = other.id
          other.save
          user.save
          response = minLoc 
        end
      end
    end 
  
    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def chooseMatch
    user1 = User.find_by_id(params[:one])
    loc1 = Location.find_by_user_id(params[:one])
    user2 = User.find_by_id(params[:two])
    loc2 = Location.find_by_user_id(params[:two])
    response = 0

    if !user1.nil? and !loc1.nil? and !user2.nil? and !loc2.nil? and user1.partner == 0 and user2.partner == 0
      user1.partner = Integer(params[:two])
      user2.partner = Integer(params[:one])
      user1.save 
      user2.save
      response = 1
    end
   
    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def showusers
    #users = User.where(:active => 1)
    users = User.all
    
    respond_to do |format|
        format.json { render :json => users }
    end
  end

  def showlocations
    locations = Location.all
    
    respond_to do |format|
        format.json { render :json => locations }
    end
  end

  def create
    oldUser = User.find_by_username(params[:username])
    response = 0

    if oldUser.nil?
      user = User.new(:name => params[:name], :year => params[:year], :username => params[:username], :password => params[:password])
      user.save
      response = user
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def destroyloc
    location = Location.find_by_user_id(params[:id])
    location.destroy

    respond_to do |format|
      if location.save
        format.json  { render :json => 1 }
      else
        format.json  { render :json => 0 }
      end
    end
  end


  def destroyuser
    user = User.find_by_id(params[:id])
    user.destroy

    respond_to do |format|
      if user.save
        format.json  { render :json => 1 }
      else
        format.json  { render :json => 0 }
      end
    end
  end

end
