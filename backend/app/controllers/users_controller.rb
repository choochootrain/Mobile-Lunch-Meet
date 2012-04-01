class UsersController < ApplicationController

  def getuser
    user = User.find_by_id(params[:id])
 
    if !user.nil?
      respond_to do |format|
        format.json { render :json => user }
      end
    else
      respond_to do |format|
        format.json { render :json => 0 }
      end
    end
  end

  def getloc
    loc = Location.find_by_user_id(params[:id])
 
    if !loc.nil?
      respond_to do |format|
        format.json { render :json => loc }
      end
    else
      respond_to do |format|
        format.json { render :json => 0 }
      end
    end
  end

  def sendloc
    @user = User.find(params[:id])
    @loc = @user.location
    @lat = Float(params[:lat])
    @long = Float(params[:long])

    if(@loc.nil?)
      @loc = Location.new(:lat => @lat, :long => @long)
      @loc.user_id = @user.id
      @user.location = @loc
      respond_to do |format|
          format.json { render :json => @loc }
      end
    else
      Location.destroy(@loc.id)
      @newLoc = Location.new(:lat => @lat, :long => @long)
      @newLoc.user_id = @user.id
      @user.location = @newLoc
      respond_to do |format|
          format.json { render :json => @loc }
      end
    end
  end

  def match
    loc = Location.find_by_user_id(params[:id])

    if loc
      minLoc = 0 
      locs = Location.all
      locs.each { |x| 
        if x.user_id != loc.user_id
          if minLoc == 0 
            minLoc = x
          elsif Math.sqrt((loc.lat - x.lat)**2 + (loc.long - x.long)**2)  < Math.sqrt((loc.lat - minLoc.lat)**2 + (loc.long - minLoc.long)**2) 
            minLoc = x
          end
        end
      }
      respond_to do |format|
        format.json { render :json => minLoc }
      end
    else
      respond_to do |format|
        format.json { render :json => 0 }
      end
    end
  end

  def showusers
    @users = User.all
    
    respond_to do |format|
        format.json { render :json => @users }
    end
  end

  def showlocations
    @locations = Location.all
    
    respond_to do |format|
        format.json { render :json => @locations }
    end
  end

  def create
    @user = User.new(:name => params[:name], :year => params[:year])

    respond_to do |format|
      if @user.save
        format.json  { render :json => @user }
      else
        format.json  { render :json => 0 }
      end
    end
  end

  def destroyloc
    @user = Location.find_by_user_id(params[:id])
    @user.destroy

    respond_to do |format|
      if @user.save
        format.json  { render :json => 1 }
      else
        format.json  { render :json => 0 }
      end
    end
  end


  def destroyuser
    @user = User.find_by_id(params[:id])
    @user.destroy

    respond_to do |format|
      if @user.save
        format.json  { render :json => 1 }
      else
        format.json  { render :json => 0 }
      end
    end
  end

end
