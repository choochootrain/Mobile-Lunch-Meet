class UsersController < ApplicationController

  def show
    @user = User.find(params[:id])
    @loc = @user.location
    @lat = Float(params[:lat])
    @long = Float(params[:long])

    if(@loc.nil?)
      @loc = Location.new(:lat => @lat, :long => @long)
      @loc.user_id = @user.id
      @user.location = @loc
    else
      Location.destroy(@loc.id)
      @newLoc = Location.new(:lat => @lat, :long => @long)
      @newLoc.user_id = @user.id
      @user.location = @newLoc
    end
 
    @users = User.all

    respond_to do |format|
        format.json { render :json => Location.all }
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
    @user = User.new()

    respond_to do |format|
      if @user.save
        format.json  { render :json => @user }
      else
        @failure = 0
        format.json  { render :json => @failure }
      end
    end
  end
 
  def destroy
    @user = User.find(params[:id])
    @user.destroy

    respond_to do |format|
      if @user.save
        format.json  { render :json => User.all }
      else
        @failure = 0
        format.json  { render :json => @failure }
      end
    end
  end

end
