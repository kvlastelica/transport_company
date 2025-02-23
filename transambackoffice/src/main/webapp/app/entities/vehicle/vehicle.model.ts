import { IRoute } from 'app/entities/route/route.model';
import { VehicleType } from 'app/entities/enumerations/vehicle-type.model';

export interface IVehicle {
  id: number;
  code?: string | null;
  description?: string | null;
  format?: keyof typeof VehicleType | null;
  routes?: IRoute[] | null;
}

export type NewVehicle = Omit<IVehicle, 'id'> & { id: null };
