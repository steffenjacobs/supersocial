import React from "react";
import { Map, TileLayer, Marker, Popup } from "react-leaflet";
import './MapContainer.css';
import './leaflet.css';
import { LocationConfiguration, UserConfigurationDecoder } from "./UserConfigurationDecoder";
import { LoginManager, LoginStatus } from "./LoginManager";
import { DeploymentManager } from "./DeploymentManager";
import { ToastManager } from "./ToastManager";
import { EventBus, EventBusEventType } from "./EventBus";

interface MapState {
  zoom: number
  lat: number
  lng: number
  location: LocationConfiguration
  changed: boolean
}

export interface MapProps {
  loginManager: LoginManager
  eventBus: EventBus
}

/** Contains a Leaflet OpenStreetMaps map. */
export class MapContainer extends React.Component<MapProps, MapState>{
  map?: Map | null;
  constructor(props: MapProps) {
    super(props);
    let loc = UserConfigurationDecoder.decodeLocation(props.loginManager);
    let lat = props.loginManager.getLoginStatus().config.find(x => x.descriptor === "user.latitude")?.value;
    let lng = props.loginManager.getLoginStatus().config.find(x => x.descriptor === "user.longitude")?.value;
    this.state = { location: loc, zoom: 5, lat: lat ? Number(lat) : 50, lng: lng ? Number(lng) : 10, changed: false };
    props.eventBus.register(EventBusEventType.USER_CHANGE, (e, u) => this.refreshState(u));
  }

  /** Called when the user clicks on the map to update the pin. */
  private onMapClick(e: any) {
    this.setState({
      lat: e.latlng.lat,
      lng: e.latlng.lng,
      changed: true
    });
  }

  /** Called when the user zooms to avoid resetting the zoom after saving. */
  private onMapZoom(e: any) {
    this.setState({ zoom: this.map ? this.map.leafletElement.getZoom() : this.state.zoom });
  }

  /**Refreshes the entire map based on the new login status information. */
  private refreshState(loginStatus: LoginStatus) {
    let loc = UserConfigurationDecoder.decodeLocationFromLoginStatus(loginStatus);
    let lat = loginStatus.config.find(x => x.descriptor === "user.latitude")?.value;
    let lng = loginStatus.config.find(x => x.descriptor === "user.longitude")?.value;
    this.setState({ location: loc, zoom: this.state.zoom, lat: lat ? Number(lat) : 50, lng: lng ? Number(lng) : 10, changed: false });
  }

  /** Persist the selected location to the backend. Updates the map and e.g. the trending twitter topics. */
  private saveSelectedLocation(e: any) {
    fetch(DeploymentManager.getUrl() + 'api/user/location', {
      method: 'PUT',
      headers: new Headers({
        'Content-Type': 'application/json'
      }),
      credentials: 'include',
      body: JSON.stringify({ latitude: this.state.lat, longitude: this.state.lng })
    })
      .then(response => {
        if (!response.ok) {
          ToastManager.showErrorToast(response);
        } else {
          response.json().then(data => {
            ToastManager.showSuccessToast("Updated location to " + data.locationName + ".");
            setTimeout(() => this.props.loginManager.logIn(), 2500);
          });
        }
      });
  }

  render() {
    return (
      <div className="mapContainer">
        <Map ref={ref => this.map = ref} onzoomlevelschange={e => this.onMapZoom(e)} onzoomend={e => this.onMapZoom(e)} onzoomstart={e => this.onMapZoom(e)} onclick={e => this.onMapClick(e)} className="mapContainer" center={this.state} zoom={this.state.zoom}>
          <TileLayer
            attribution='&amp;copy <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <Marker position={this.state}>
            <Popup>
              <b>Your {this.state.changed ? "previously " : ""} selected location:</b><br /><br />
              {this.state.location.name} ({this.state.location.placeType.name})<br />
              {this.state.location.country}
              {this.state.changed ? (<span><br /><br />Click the <b>save</b> button below to store your updated location.</span>) : ""}
            </Popup>
          </Marker>
        </Map>
        <button className="btn btn-primary send-button" onClick={this.saveSelectedLocation.bind(this)}>Save</button>
      </div>
    );
  }
}