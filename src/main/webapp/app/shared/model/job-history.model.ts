import { type IJob } from '@/shared/model/job.model';
import { type IDepartment } from '@/shared/model/department.model';
import { type IEmployee } from '@/shared/model/employee.model';

import { type Language } from '@/shared/model/enumerations/language.model';
export interface IJobHistory {
  id?: number;
  startDate?: Date | null;
  endDate?: Date | null;
  language?: keyof typeof Language | null;
  fileContentType?: string | null;
  file?: string | null;
  date?: Date | null;
  duration?: string | null;
  job?: IJob | null;
  department?: IDepartment | null;
  employee?: IEmployee | null;
}

export class JobHistory implements IJobHistory {
  constructor(
    public id?: number,
    public startDate?: Date | null,
    public endDate?: Date | null,
    public language?: keyof typeof Language | null,
    public fileContentType?: string | null,
    public file?: string | null,
    public date?: Date | null,
    public duration?: string | null,
    public job?: IJob | null,
    public department?: IDepartment | null,
    public employee?: IEmployee | null,
  ) {}
}
