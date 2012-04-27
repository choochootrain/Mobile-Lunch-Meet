class UsersController < ApplicationController

  def partner
    user = User.find_by_id(params[:id])
    response = -1

    if user.nil?
      response = -1 
    else
      if user.active == 0
        response = -1 
      else
        if user.partner == 0 
          response = -1
        else
          response = user.partner
        end
      end
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def changeinfo
    user = User.find_by_id(params[:id])
    response = -1

    if user.nil?
      response = 0  
    else
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
    response = -1
   
    if user.nil?
      response = 3 # no such user
    else
      if user.active == 1
        response = 2 # already logged on
      else
        if user.password == params[:password] 
          user.active = 1 
          user.save
          response = 1 # success
        else
          response = 0 # incorrect password
        end
      end
    end

    respond_to do |format|
      format.json { render :json => response }
    end
  end

  def logout
    user = User.find_by_username(params[:username])
    response = -1

    if user.nil?
      response = 3 # no such user
    else
      if user.active == 1
        user.active = 0
        user.location.destroy
        partner = user.partner 
        user.partner = 0 
        otherUser = User.find_by_id(partner)
        otherUser.partner = 0
        otherUser.save
        user.save
        response = 1 # succesful log out 
      else
        response = 0 # not logged in
      end
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
    response = -1

    if !user.nil?
        if user.active == 0
          response = 0
        else 
          loc = user.location
          lat = Float(params[:lat])
          long = Float(params[:long])
  
          if(loc.nil?)
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
    else
      # user is nil
      response = 0
      respond_to do |format|
          format.json { render :json => response }
      end
    end
  end

  def closestMatch
    user = User.find_by_id(params[:id])

    if user.nil? or user.active == 0 
      respond_to do |format|
        format.json { render :json => 0 }
      end
    else
      loc = Location.find_by_user_id(params[:id])

      if !loc.nil?
        minLoc = 0 
        activeUsers = User.where(:active => 1)
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

        if minLoc != 0
          other = User.find_by_id(minLoc.user_id) 
          other.partner = user.id
          user.partner = other.id
          other.save
          user.save
        end
        respond_to do |format|
          format.json { render :json => minLoc }
        end
      else
        respond_to do |format|
          format.json { render :json => 0 }
        end
      end
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

    if !oldUser.nil?
      respond_to do |format|
        format.json { render :json => 0 }
      end
    else
      user = User.new(:name => params[:name], :year => params[:year], :username => params[:username], :password => params[:password])
      respond_to do |format|
        if user.save
          format.json  { render :json => user }
        else
          format.json  { render :json => 0 }
        end
      end
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
